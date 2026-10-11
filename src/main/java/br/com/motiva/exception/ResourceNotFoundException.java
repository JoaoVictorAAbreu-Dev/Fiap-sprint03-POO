package br.com.motiva.exception;
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String recurso, Long id) { super(recurso + " " + id + " nao encontrado."); }
}
