//testira pozive funkcije unutar petlje

int minus_jedan(int a) {
    return a - 1;
}

int main(void) {
    int x = 20, a = 5;

    while (a > 0) {
        x = minus_jedan(x);
    }
    return x;   //15 u R6
}