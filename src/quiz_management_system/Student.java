/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quiz_management_system;
import java.util.Scanner;
import quiz_management_system.Quiz.Question;

/**
 *
 * @author belsa
 */
public class Student
{
    private int userID;
    private String username;
    private String password;
    protected Attempt attempted; 
            
    class Attempt
    {
        private Quiz quiz;
        private Question[] model; 
        private double grade;
        
        public Attempt(Quiz newQuiz)
        {
            this.quiz = newQuiz;
            model = new Question[quiz.getnQuestions()];
            model = quiz.generateQuizModel();
            grade = 0;
        }
        
        public void doAttempt()
        {
            Scanner sc = new Scanner(System.in);
            short answerIndex;
            
            System.out.println("Starting Quiz...Enter answer index after the prompt appears.");
            
            for(int i = 0; i < quiz.getnQuestions(); i++)
            {
                model[i].displayQuestion();
                answerIndex = sc.nextShort();
                
                if(model[i].checkAnswer(answerIndex))
                    grade += model[i].getGrade();
            }
        }
    }
    
    public void startAttempt(Quiz newQuiz)
    {
        Attempt newAttempt = new Attempt(newQuiz);
        newAttempt.doAttempt();
    }
    
    public String getUsername()
    {
        return username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }
    public String getPassword()
    {
        return password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }

}