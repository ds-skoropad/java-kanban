package ru.yandex.practicum.kanban.http.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.kanban.http.HttpServerGsonException;
import ru.yandex.practicum.kanban.manager.ManagerSaveException;
import ru.yandex.practicum.kanban.util.TaskUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BaseHttpHandler {

    public enum RequestMethod {
        GET,
        POST,
        DELETE,
        UNKNOWN
    }

    public void runRoute(HttpExchange h, Route r) throws IOException {
        RouteExchange routeExchange = new RouteExchange(new String(h.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8));

        // Поиск подходящего пути
        boolean pathMatch = false;
        while (r != null) {
            String[] uriH = h.getRequestURI().getPath().split("/"); // Разделенный uri exchange
            String[] uriR = r.uri.split("/"); // Разделенный uri route
            // Проверка совпадения пути
            if (uriH.length == uriR.length) {
                if (r.uri.contains("{") && r.uri.contains("}")) { // Присутствует параметр
                    pathMatch = true;
                    for (int i = 0; i < uriR.length; i++) {
                        if (!uriR[i].contains("{") && !uriR[i].contains("}")) { // Не параметр, значит сравниваем
                            if (!uriR[i].equals(uriH[i])) {
                                pathMatch = false;
                            }
                        } else { // Параметр. Не сравниваем.
                            String key = uriR[i].substring(uriR[i].indexOf("{") + 1, uriR[i].indexOf("}"));
                            if (!key.isBlank()) {
                                routeExchange.keys.put(key, (uriH[i] == null) ? "" : uriH[i]);
                            }
                        }
                    }
                } else { // Параметра нет, можно сравнить целиком
                    if (r.uri.equals(h.getRequestURI().getPath())) {
                        pathMatch = true; // Есть совпадение пути
                    }
                }
                // Проверка метода
                if (pathMatch && r.requestMethod == getMethod(h)) break; // Маршрут найден
            }
            r = r.previous;
        }
        if (r == null) {
            if (!pathMatch) { // Не найден путь
                send(h, sendNotFound());
            } else { // Иначе не найден метод
                send(h, sendMethodNotAllowed());
            }
            return; // Маршрут не найден
        }
        // Выполнение найденного маршрута
        HandlerExchange handlerExchange;
        try {
            handlerExchange = r.routeExchangeCustomer.apply(routeExchange);
        } catch (HttpServerGsonException e) {
            handlerExchange = sendNotAcceptable();
        } catch (ManagerSaveException e) {
            handlerExchange = sendInternalServerError();
        }
        send(h, handlerExchange);
    }

    private RequestMethod getMethod(HttpExchange h) {
        String method = h.getRequestMethod();
        return (TaskUtils.stringInArrEnum(RequestMethod.values(), method)) ?
                RequestMethod.valueOf(method) : RequestMethod.UNKNOWN;

    }

    public void send(HttpExchange h, HandlerExchange handlerExchange) throws IOException {
        byte[] response = handlerExchange.text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        h.sendResponseHeaders(handlerExchange.rCode, response.length);
        h.getResponseBody().write(response);
        h.close();
    }

    public HandlerExchange sendSuccess(String text) {
        return new HandlerExchange(200, text);
    }

    public HandlerExchange sendSuccessModify(String text) {
        return new HandlerExchange(201, text);
    }

    public HandlerExchange sendNotFound() {
        return new HandlerExchange(404, "Not Found");
    }

    public HandlerExchange sendMethodNotAllowed() {
        return new HandlerExchange(405, "Method Not Allowed");
    }

    public HandlerExchange sendNotAcceptable() {
        return new HandlerExchange(406, "Not Acceptable");
    }

    public HandlerExchange sendInternalServerError() {
        return new HandlerExchange(500, "Internal Server Error ");
    }

    // Класс ответа маршрута
    public static class RouteExchange {
        private final Map<String, String> keys;
        private final String requestBody;

        public RouteExchange(String requestBody) {
            this.keys = new HashMap<>();
            this.requestBody = requestBody;
        }

        public Map<String, String> getKeys() {
            return keys;
        }

        public String getRequestBody() {
            return requestBody;
        }
    }

    // Класс ответа обработчика
    public static class HandlerExchange {
        private final int rCode;
        private final String text;

        public HandlerExchange(int rCode, String text) {
            this.rCode = rCode;
            this.text = text;
        }
    }

    // Класс маршрута
    public static class Route {
        private Route previous;
        private RequestMethod requestMethod;
        private String uri;
        private Function<RouteExchange, HandlerExchange> routeExchangeCustomer;

        public Route add(String uri, RequestMethod requestMethod,
                         Function<RouteExchange, HandlerExchange> routeExchangeCustomer) {
            this.requestMethod = requestMethod;
            this.uri = uri;
            this.routeExchangeCustomer = routeExchangeCustomer;

            Route route = new Route();
            route.previous = this;
            return route;
        }

        public Route build() {
            return (previous == null) ? null : previous;
        }
    }
}

