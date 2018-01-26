//testira jednostavnu rekurziju

int rek(int a);

int main(void) {
    int x = 4;
    x = rek(x);
    return x;   //10 u R6 valjda
}

int rek(int a) {
    if (a == 0) {
        return 0;
    }
    return a + rek(a - 1);
}