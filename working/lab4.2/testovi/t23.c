//testira chararray kao string

int main(void) {
    char str[8] = "coconut";
    int i, ctr;
    char c = 'c';

    for (i = 0, ctr = 0; i < 30; i++) {
        if (str[i] == c) ctr++;
    }
    return ctr; //2 u R6
}