package net.iozamudioa.fueler.application.ports.out;

public interface TokenOutputPort {

    String issue();

    String hash(String token);
}