package com.hemobit.hemobit.service;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

final class RepositoriosEmMemoria {
    private RepositoriosEmMemoria() {
    }

    @SuppressWarnings("unchecked")
    static <R> R criar(Class<R> repositorio, Map<Long, Object> dados,
                       Map<String, Function<Object[], Object>> extras) {
        return (R) Proxy.newProxyInstance(repositorio.getClassLoader(), new Class<?>[]{repositorio},
                (proxy, metodo, args) -> {
                    if (metodo.getDeclaringClass() == Object.class) {
                        return switch (metodo.getName()) {
                            case "equals" -> proxy == args[0];
                            case "hashCode" -> System.identityHashCode(proxy);
                            default -> "repositorio-em-memoria";
                        };
                    }
                    return switch (metodo.getName()) {
                        case "findAll" -> new ArrayList<>(dados.values());
                        case "findById" -> Optional.ofNullable(dados.get((Long) args[0]));
                        case "save" -> salvar(dados, args[0]);
                        case "saveAll" -> {
                            List<Object> salvos = new ArrayList<>();
                            for (Object item : (Iterable<?>) args[0]) {
                                salvos.add(salvar(dados, item));
                            }
                            yield salvos;
                        }
                        case "deleteById" -> {
                            dados.remove((Long) args[0]);
                            yield null;
                        }
                        case "existsById" -> dados.containsKey((Long) args[0]);
                        default -> {
                            Function<Object[], Object> consulta = extras.get(metodo.getName());
                            if (consulta == null) {
                                throw new UnsupportedOperationException(metodo.getName());
                            }
                            yield consulta.apply(args);
                        }
                    };
                });
    }

    static void definirId(Object entidade, long id) {
        try {
            Field campo = campoId(entidade.getClass());
            campo.setAccessible(true);
            campo.set(entidade, id);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Object salvar(Map<Long, Object> dados, Object entidade) throws ReflectiveOperationException {
        Field campo = campoId(entidade.getClass());
        campo.setAccessible(true);
        if (campo.get(entidade) == null) {
            long proximo = dados.keySet().stream().mapToLong(Long::longValue).max().orElse(0L) + 1;
            campo.set(entidade, proximo);
        }
        dados.put((Long) campo.get(entidade), entidade);
        return entidade;
    }

    private static Field campoId(Class<?> tipo) throws NoSuchFieldException {
        for (Class<?> c = tipo; c != null; c = c.getSuperclass()) {
            try {
                return c.getDeclaredField("id");
            } catch (NoSuchFieldException ignorado) {
            }
        }
        throw new NoSuchFieldException("id");
    }
}
