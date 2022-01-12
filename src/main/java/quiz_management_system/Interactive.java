package quiz_management_system;

import java.text.ParseException;

public interface Interactive
{
    void listMenu() throws ParseException;

    /**
     * @deprecated
     */
    int listMenuConsole();
}
