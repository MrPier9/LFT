public class Esercizio17
{
    public static boolean scan(String s)
    {
	int state = 0;
	int i = 0;

    while (state >= 0 && i < s.length()) {
	    final char ch = s.charAt(i++);

        switch (state){
        case 0:
        if(ch == 'P' || ch == 'p')
            state = 1;
        else if(ch != 'P' || ch != 'p')
            state = 6;
        else
            state = -1;
        break;

        case 1:
        if(ch == 'A' || ch == 'a')
            state = 2;
        else if(ch != 'A' || ch != 'a')
            state = 7;
        else
            state = -1;
        break;

        case 2:
        if(ch == 'O' || ch == 'o')
            state = 3;
        else if(ch != 'O' || ch != 'o')
            state = 8;
        else
            state = -1;
        break;
        
        case 3:
        if(ch == 'L' || ch == 'l')
            state = 4;
        else if(ch != 'L' || ch != 'l')
            state = 9;
        else
            state = -1;
        break;

        case 4:
        if(ch == 'O' || ch == 'o')
            state = 5;
        else if(ch != 'O' || ch != 'o')
            state = 10;
        else
            state = -1;
        break;

        /*case 5:
        if(ch >= 'L' && ch <= 'Z')
            state = 7;
        else if((ch % 2) != 0)
            state = 6;
        else if((ch % 2) == 0)
            state = 5;
        else
            state = -1;
        break;*/

        case 6:
        if(ch == 'A' || ch == 'a')
            state = 7;
        else
            state = -1;
        break;

        case 7:
        if(ch == 'O' || ch == 'o')
            state = 8;
        else
            state = -1;
        break;
        
        case 8:
        if(ch == 'L' || ch == 'l')
            state = 9;
        else
            state = -1;
        break;

        case 9:
        if(ch == 'O' || ch == 'o')
            state = 10;
        else
            state = -1;
        break;
        }
        System.out.println(state);
    }
    return state == 5 || state == 10;
    }

    public static void main(String[] args)
    {
	System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}