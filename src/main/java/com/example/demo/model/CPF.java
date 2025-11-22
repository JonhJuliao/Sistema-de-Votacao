package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class CPF {

    @Column(name = "cpf", length = 11, nullable = false, unique = true)
    private String valor;

    // Construtor protegido exigido pelo JPA
    protected CPF() {}

    public CPF(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("CPF não pode ser nulo ou vazio");
        }

        String cpfLimpo = valor.replaceAll("\\D", "");

        if (!cpfLimpo.matches("\\d{11}")) {
            throw new IllegalArgumentException("CPF deve conter exatamente 11 dígitos");
        }

        if (!isCpfValido(cpfLimpo)) {
            throw new IllegalArgumentException("CPF inválido");
        }

        this.valor = cpfLimpo;
    }

    public String getValor() {
        return valor;
    }

    public String formatado() {
        return String.format("%s.%s.%s-%s",
                valor.substring(0, 3),
                valor.substring(3, 6),
                valor.substring(6, 9),
                valor.substring(9, 11));
    }

    @Override
    public String toString() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CPF)) return false;
        CPF cpf = (CPF) o;
        return Objects.equals(valor, cpf.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }


    // ------------------------------------------------------
    // Validação oficial de CPF (módulo 11)
    // ------------------------------------------------------
    private boolean isCpfValido(String cpf) {
        // CPFs com todos os dígitos iguais são inválidos (ex: 11111111111)
        if (cpf.chars().distinct().count() == 1) {
            return false;
        }

        return verificaDigito(cpf, 9) && verificaDigito(cpf, 10);
    }

    private boolean verificaDigito(String cpf, int posicao) {
        int soma = 0;
        int peso = posicao + 1;

        for (int i = 0; i < posicao; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (peso - i);
        }

        int resto = (soma * 10) % 11;
        int digitoEsperado = (resto == 10) ? 0 : resto;

        return digitoEsperado == Character.getNumericValue(cpf.charAt(posicao));
    }
}
