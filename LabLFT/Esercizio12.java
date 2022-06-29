public class Esercizio12
{
    public static boolean scan(String s)
    {
	int state = 0;
	int i = 0;

    while (state >= 0 && i < s.length()) {
	    final char ch = s.charAt(i++);

        switch (state){
        case 0:
        if(ch == '_')
            state = 0;
        else if(ch >= 'a' && ch <= 'z')
            state = 1;
        else 
            state = -1;
        break;

        case 1:
        if(ch == '_' || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9'))
            state = 1;
        else 
            state = -1;
        break;
        }
    }
    return state == 1;
    }

    public static void main(String[] args)
    {
	System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}