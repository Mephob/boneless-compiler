//testira lokalno polje

int main(void) {
    int a[6];

    a[0] = 3;
    a[5] = 99999;

    return a[0] - a[5];     //-99996 u R6?
}