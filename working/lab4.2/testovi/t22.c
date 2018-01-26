//testira koristenje globalne varijable u funkciji 

int glob = 25;

int probaj_globalnu(int a) {
    glob = glob + a;
    return glob / 2;
}

int main(void) {
    int x = 3;
    return probaj_globalnu(x);  //14 u R6
}