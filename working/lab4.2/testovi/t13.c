//testira for petlju s breakom

int main(void) {
    int b = 1, i;

    for (i = 0; i < 5; i++) {
        b = b * 2;
        if (b == 8) {
            break;
        }
    }

    return b;   //8 u R6
}