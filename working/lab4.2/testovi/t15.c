//testira jednostavnu funkciju bez povratne vrijednosti

void fun(int a);

int main(void) {
    int x = 4;
    fun(x);
    return 0;   //0 u R6
}

void fun(int a) {
    int b = a + 2;
    b++;
}