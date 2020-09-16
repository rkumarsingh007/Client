
import java.util.Scanner;
import java.lang.Character;
public  class Main
{
 public static void main(String[] args)
{
    Scanner sc=new Scanner(System.in);
    char ch;
    String line=sc.nextLine();
    String lin=line.toLowerCase();
    int ucnt=0,scnt=0;
    int[] arr=new int[26];
    for(int i=0;i<lin.length();i++)
    {
        ch=lin.charAt(i);
        if(ch!=' '&&!Character.isLetter(ch))
        {
            System.out.println("Invalid slogan");
            return;
        }
        else if(ch!=' ')
        arr[(int)ch-'a']++;
    }
    for(int x:arr)
    {
        if(x==1)
            ucnt++;
        else if(x>1)
        {
            scnt+=x;
        }
            
    }
     if(ucnt==scnt)
     {
         System.out.println("All the guidelines are satisfied for "+line);
     }
     else 
         System.out.println(line+" does not satisfy the guideline");

}
}
