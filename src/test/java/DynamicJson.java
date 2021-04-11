import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DynamicJson {

    @Test(dataProvider="BooksData")
    public void addBook(String isbn, String aisle) {
        RestAssured.baseURI="http://216.10.245.166";
        String response =
                RestAssured.given().log().all()
                    .header("Content-Type", "application/json")
                    .body(payload.addBook(isbn,aisle))
                .when()
                    .post("/Library/Addbook.php")
                .then().log().all()
                    .assertThat().statusCode(200)
                    .extract().response().asString();

        JsonPath js = new JsonPath(response);
        String id = js.get("ID");

        System.out.println(id);
    }

    @DataProvider(name="BooksData")
    public Object[][] getData() {

        return new Object[][] {{"jhyht","8521"},{"çooli","4128"},{"oolpo","7415"}};
    }
}
