#include <stdio.h>

// Funcao para calcular o fatorial de um numero
double fatorial(int num) {
    if (num <= 0) {
        return 0; // Fatorial de numeros negativos nao eh definido
    }
    double resultado = 1.0; // Use 1.0 para garantir a operacao com ponto flutuante
    int i;
    for (i = 1; i <= num; i++) {
        resultado *= i;
    }
    return resultado;
}

int main() {
    int n, k;
    double resultado;

    printf("Calculo de Combinatoria (C(n, k))\n");
    printf("Digite o valor de n (total de elementos): ");
    scanf("%d", &n);
    printf("Digite o valor de k (elementos a serem escolhidos): ");
    scanf("%d", &k);

    if (k > n || k < 0) {
        printf("Erro: k deve ser menor ou igual a n, e k deve ser nao-negativo.\n");
        return 1;
    }

    // C(n, k) = n! / (k! * (n-k)!)
    resultado = fatorial(n) / (fatorial(k) * fatorial(n - k));

    
    printf("A combinatoria de %d elementos tomados %d a %d eh: %.0f\n", n, k, k, resultado);

    return 0;
}
