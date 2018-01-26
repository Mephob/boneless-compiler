//funkcija koja ima petlju bez breaka, s povratnom vrijednosti

int fun(void) {
    int c = 0;
    int i = 0;
    for (i = 0; i < 10; i = i + 1) {
        c = c + 1;
        if (c == 5) {
            return c;
        }
    }
}

int main(void) {
    int x = 4;
    x = fun();
    return x;   //5 u R6
}