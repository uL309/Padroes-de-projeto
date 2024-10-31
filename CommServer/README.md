## Pontos de melhoria:

## Padrão 1: Refatoração, Separar Responsabilidades
    1. Na thread principal do app separar a interface
    2. no commThread separar os comandos

## Padrão 2: Singleton

    3. Usar um Singleton para representar a lista de pessoas conectadas para todas as threads e o console principal, é repetido nas duas classes comm.

## Padrão 3: Refatoração, Rever Classes (Abstract Factory ou Builder)

    4. unificar o console principal com commThread,fazendo uma classe unica e implementar as diferenças estendendo ela para os dois

## Padrão 4: Implementar um MVC
    5. neste caso seria readequar o projeto inteiro para o modelo onde os dados se desacoplam das funcionalidades do servidor e do console do programa.
