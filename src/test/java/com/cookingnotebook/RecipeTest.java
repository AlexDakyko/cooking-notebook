import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class RecipeTest {

    @Test
    public void testValidateRecipeTitleNull() {
        RecipeController controller = new RecipeController();


        assertFalse("Should return false for null title", controller.validateRecipeTitle(null));
    }

    @Test
    public void testValidateRecipeTitleEmpty() {
        RecipeController controller = new RecipeController();
        assertFalse("Should return false for empty title", controller.validateRecipeTitle(""));
    }

    @Test
    public void testValidateRecipeTitleWhitespace() {
        RecipeController controller = new RecipeController();
        assertFalse("Should return false for whitespace-only title", controller.validateRecipeTitle("   "));
    }

    @Test
    public void testValidateRecipeTitleValid() {
        RecipeController controller = new RecipeController();
        assertTrue("Should return true for valid title", controller.validateRecipeTitle("Sample Recipe"));
    }
}
