//testira jednostavnu funkciju s povratnom vrijednosti

int fun(int a) {
    int b = a + 2;
    b++;

    return b;
}

int main(void) {
    int x = 4;
    x = fun(x);
    return x;   //7 u R6
}