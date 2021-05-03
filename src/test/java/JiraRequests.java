import static io.restassured.RestAssured.*;
import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.testng.Assert;

import java.io.File;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JiraRequests {

    SessionFilter session;

    @BeforeAll
    public void init() {

        RestAssured.baseURI = "http://localhost:8080";

        session = new SessionFilter();

        given().relaxedHTTPSValidation().header("Content-Type", "application/json")
                .body("{ \"username\": \"btozetto\", \"password\": \"Brujira0!\" }")
                .filter(session)
        .when().log().all()
                .post("/rest/auth/1/session")
        .then().log().all();
    }

    @Test
    @DisplayName("Adiciono um comentário em uma issue existente")
    public void quandoAdicionoComentarioEmIssueExistente() {

        String expectedMessage = "Hi, how are you?";

        String addCommentResponse =
        given().pathParam("key", "10000").log().all()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"body\": \"" + expectedMessage + "\",\n" +
                        "    \"visibility\": {\n" +
                        "        \"type\": \"role\",\n" +
                        "        \"value\": \"Administrators\"\n" +
                        "    }\n" +
                        "}")
                .filter(session)
        .when()
                .post("/rest/api/2/issue/{key}/comment")
        .then().log().all()
                .assertThat().statusCode(201)
                .extract().response().asString();

        JsonPath js = new JsonPath(addCommentResponse);
        String commentId = js.getString("id");

        String issueDetails =
        given().pathParam("key", "10000")
            .queryParam("fields", "comment")
            .filter(session).log().all()
        .when()
            .get("/rest/api/2/issue/{key}")
        .then().log().all()
            .assertThat().statusCode(200)
            .extract().response().asString();

        js = new JsonPath(issueDetails);

        int commentsCount = js.getInt("fields.comment.comments.size()");

        for (int i=0; i < commentsCount; i++) {

            if ((js.get("fields.comment.comments[" + i + "].id").toString()).equals(commentId)){

                String message = js.get("fields.comment.comments[" + i + "].body").toString();
                Assert.assertEquals(message, expectedMessage);
            }
        }
    }

    @Test
    @DisplayName("Adiciono um arquivo em anexo")
    public void quandoAdicionoArquivoAnexo() {

        given().pathParam("key", "10000").log().all()
                .header("X-Atlassian-Token", "no-check")
                .header("Content-Type", "multipart/form-data")
                .multiPart("file", new File("C:\\Users\\Public\\Projetos\\teste-api-restassured-java-testng\\src\\test\\resources\\jiraFile.txt"))
                .filter(session)
        .when()
                .post("/rest/api/2/issue/{key}/attachments")
        .then().log().all()
                .assertThat().statusCode(200);
    }

    @Test
    @DisplayName("Obtenho dados de uma issue")
    public void quandoObtenhoDadosDaIssue() {

        String response =
        given().pathParam("key", "10000")
                .queryParam("fields", "comment")
                .filter(session).log().all()
        .when()
                .get("/rest/api/2/issue/{key}")
                .then().log().all()
                .assertThat().statusCode(200)
                .extract().response().asString();
    }
}
