//testira nestane pozive

int minus_jedan(int a) {
    return a - 1;
}

int minus_dva(int b) {
    return b - 2;
}

int minus_tri(int c) {
    return c - 3;
}

int minus_cetiri(int d) {
    return d - 4;
}

int main(void) {
    int x = 20;

    return minus_cetiri(minus_tri(minus_dva(minus_jedan(x)))); //10 u R6
}