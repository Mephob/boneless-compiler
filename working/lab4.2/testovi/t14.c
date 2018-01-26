//testira for petlju u while petlji

int main(void) {
    int a = 0, b = 0, i;

    while (a < 2) {
        for (i = 0; i < 3; i++) {
            b = b + 1;
        }
        a += 1;
    }

    return b;   //6 u R6
}