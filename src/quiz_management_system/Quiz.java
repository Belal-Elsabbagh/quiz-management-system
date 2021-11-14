package quiz_management_system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;

public class Quiz implements Serializable
{

    private int quizID;
    private String quizTitle;
    private Teacher creator;
    private int nAttempts, nQuestions;
    private Calendar openTime, duration; //still uncertain of the data type.
    private ArrayList<Question> questionBank;

    public Quiz()
    {
        questionBank = new ArrayList();
    }
    /************************************************** Setters & Getters *****/
    public int getQuizID()
    {
        return quizID;
    }

    public String getQuizTitle()
    {
        return quizTitle;
    }
    public void setQuizTitle(String quizTitle)
    {
        this.quizTitle = quizTitle;
    }
    public int getnAttempts()
    {
        return nAttempts;
    }
    public void setnAttempts(int nAttempts)
    {
        this.nAttempts = nAttempts;
    }
    public int getnQuestions()
    {
        return nQuestions;
    }
    public void setnQuestions(int nQuestions)
    {
        this.nQuestions = nQuestions;
    }
    public Teacher getCreator()
    {
        return creator;
    }
    public void setCreator(Teacher creator)
    {
        this.creator = creator;
    }
    /********************************************End of Setters & Getters *****/
    
    public void displayQuizProperties()
    {
        System.out.println("-----------Displaying quiz properties------------");
        System.out.println("Quiz ID: " + quizID);
        System.out.println("Quiz Title: " + quizTitle);
        System.out.println("Number of questions: " + nQuestions);
        System.out.println("Number of attempts: " + nAttempts);
        System.out.println("Size of question bank: " + questionBank.size());
    }

    @Override
    public String toString()
    {
        return "Quiz{" + "quizID=" + quizID + ", quizTitle=" + quizTitle + ", nAttempts=" + nAttempts + ", nQuestions=" + nQuestions + ", questionBank.size()=" + questionBank.size() + '}';
    }

    public void createQuiz(Teacher author)
    {
        Scanner sc = new Scanner(System.in);
        creator = author;

        System.out.println("-----------Creating a new quiz-------------------");
        System.out.println("Enter quiz title: ");
        quizTitle = sc.nextLine();

            System.out.println("Enter number of questions: ");
            nQuestions = sc.nextInt();

            System.out.println("Enter number of attempts: ");
            nAttempts = sc.nextInt();

//                System.out.println("Enter size of question bank: ");
//                questionBank = new ArrayList(sc.nextInt());
        createQuestionBank();

        System.out.println("Quiz created sucessfully.");
    }
    
    public void createQuestionBank() 
    {
        questionBank = new ArrayList();
        Scanner sc = new Scanner(System.in);

        System.out.println("-----------Question Bank Creator-----------------");
        int i = 1;
        while(i != 0)
        {
            Question tempQ = new Question();
            tempQ.createQuestion();
            questionBank.add(tempQ);
            System.out.println("stop?");
            i = sc.nextInt();
        }
        System.out.println("Question Bank created successfully.");
    }

    public Question[] generateQuizModel()
    {
        Question[] newModel;
        newModel = new Question[nQuestions];

        Random generator = new Random();
        int randIndex;
        for (int i = 0; i < nQuestions; i++)
        {
            randIndex = generator.nextInt(questionBank.size());

            newModel[i] = questionBank.get(randIndex);
        }
        return newModel;
    }

    public class Question implements Serializable
    {

        private int questionID;
        private String prompt;
        private double grade;
        private Choice mcq;

        public Question()
        {
            
        }

        public void createQuestion()
        {
            Scanner sc = new Scanner(System.in);
            System.out.println("-----------Creating a new question-----------");
            System.out.println("Enter prompt: ");
            prompt = sc.next();

            try
            {
                sc = new Scanner(System.in);
                System.out.println("Enter grade: ");
                grade = sc.nextDouble();
            } catch (Exception e)
            {
                System.out.println("Error: Invailed input");
                return;
            }

            mcq = new Choice();
            mcq.createMCQ();

            System.out.println("Question added successfully.");

        }

        public int getQuestionID()
        {
            return questionID;
        }

        public String getPrompt()
        {
            return prompt;
        }

        public double getGrade()
        {
            return grade;
        }

        public Choice getMcq()
        {
            return mcq;
        }

        public void displayQuestion()
        {
            System.out.println(prompt);
            for (int i = 0; i < mcq.nChoices; i++)
            {
                System.out.println((i + 1) + ". " + mcq.choices[i]);
            }
        }
                
        public boolean checkAnswer(short inAnswer)
        {
            boolean result = false;
            if (inAnswer == mcq.getAnswerKeyIndex())
            {
                result = true;
            }
            return result;
        }
    }

    private class Choice implements Serializable
    {

        private int nChoices;
        private String[] choices;
        private int answerKeyIndex;

        private Choice()
        {
            this.nChoices = 0;
            choices = new String[2];
        }

        public int getnChoices()
        {
            return nChoices;
        }

        public int getAnswerKeyIndex()
        {
            return answerKeyIndex;
        }

        public void createMCQ()
        {

            Scanner sc = new Scanner(System.in);

            System.out.println("-----------Creating MCQ------------------");
            try
            {
                System.out.println("Enter number of choices: ");
                nChoices = sc.nextShort();

            } catch (Exception e)
            {
                System.out.println("Error: Invailed input");
                return;
            }
            choices = new String[nChoices];

            for (int i = 0; i < nChoices; i++)
            {
                System.out.println("Enter choice " + i + ": ");
                choices[i] = sc.next();
            }

            answerKeyIndex = nChoices;
            try
            {
                while (answerKeyIndex > nChoices - 1)
                {
                    System.out.println("Enter the index of right answer (count starts at 0): ");
                    answerKeyIndex = sc.nextShort();
                    if (answerKeyIndex > nChoices - 1)
                    {
                        System.out.println("Error: The number of choice doesn't exist");
                    }
                }
            } catch (Exception e)
            {
                System.out.println("Error: Invailed input");
                return;
            }
            System.out.println("Choices added successfully.");
        }

        public boolean checkAnswer(int inAnswer)
        {
            boolean result = false;
            if (inAnswer == answerKeyIndex)
            {
                result = true;
            }
            return result;
        }
    }
}