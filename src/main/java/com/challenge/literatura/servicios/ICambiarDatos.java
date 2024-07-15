package com.challenge.literatura.servicios;

public interface ICambiarDatos {
    <T> T obtenerDatos(String json, Class<T> clase);
}
