import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class TestaAPI {

    @Test
    @DisplayName("Testo a API do Curso")
    public void quandoTestarAPICursoComPayload() {

        JsonPath js = new JsonPath(payload.CoursePrice());

        int countCourses = js.getInt("courses.size()");
        System.out.println(countCourses);

        int totalAmount = js.getInt("dashboard.purchaseAmount");
        System.out.println(totalAmount);

        String firstCourseName = js.get("courses[0].title");
        System.out.println(firstCourseName);

        for (int i = 0; i < countCourses; i++) {

            System.out.println(js.get("courses["+i+"].title").toString());
            System.out.println(js.get("courses["+i+"].price").toString());
        }

        for (int i = 0; i < countCourses; i++) {

            if (js.get("courses["+i+"].title").toString().equalsIgnoreCase("RPA")) {
                System.out.println(js.get("courses["+i+"].copies").toString());
                break;
            }
        }

        int sumAllCourses = 0;

        for (int i = 0; i < countCourses; i++) {

            for (int i2 = 0; i2 < js.getInt("courses["+i+"].copies"); i2++) {

                sumAllCourses = sumAllCourses + js.getInt("courses["+i+"].price");
            }
        }

        Assert.assertEquals(sumAllCourses, totalAmount);
    }

    @Test
    @DisplayName("Testo a API do Curso")
    public void quandoTestarAPICurso() {

        RestAssured.baseURI = "https://rahulshettyacademy.com";
        String corpoRequisicao = "{\n" +
                "  \"location\": {\n" +
                "    \"lat\": -69.383494,\n" +
                "    \"lng\": 11.427362\n" +
                "  },\n" +
                "  \"accuracy\": 50,\n" +
                "  \"name\": \"Allianz Park\",\n" +
                "  \"phone_number\": \"(+69) 171 444 3214\",\n" +
                "  \"address\": \"11, Palestra Italia\",\n" +
                "  \"types\": [\n" +
                "    \"soccer field\",\n" +
                "    \"green field\"\n" +
                "  ],\n" +
                "  \"website\": \"http://google.com\",\n" +
                "  \"language\": \"Brazil-PT\"\n" +
                "}";

        String corpoAtualizadoRequisicao = "{\n" +
                "  \"place_id\": \"@placeId\",\n" +
                "  \"address\": \"@newAddress\",\n" +
                "  \"key\": \"qaclick123\"\n" +
                "}";

        String response =

                given().log().all()
                        .queryParam("key", "qaclick123")
                        .header("Content-Type", "application/json")
                        .body(corpoRequisicao)
                        .when()
                        .post("maps/api/place/add/json")
                        .then()
                        .assertThat().statusCode(200)
                        .body("scope", equalTo("APP"))
                        .header("Server", "Apache/2.4.18 (Ubuntu)")
                        .extract().response().asString();

        System.out.println(response);
        JsonPath js = new JsonPath(response);
        String placeId = js.getString("place_id");
        System.out.println(placeId);

        //Update Place
        String newAddress = "66, Endereço Mais Novo";

        given().log().all()
                .queryParam("key", "qaclick123")
                .header("Content-Type", "application/json")
                .body(corpoAtualizadoRequisicao.replace("@placeId", placeId).replace("@newAddress", newAddress))
                .when()
                .put("maps/api/place/update/json")
                .then()
                .assertThat().log().all().statusCode(200)
                .body("msg", equalTo("Address successfully updated"));

        //Get Place
        String getPlaceResponse = given().log().all()
                .queryParam("key", "qaclick123")
                .queryParam("place_id", placeId)
                .when()
                .get("maps/api/place/get/json")
                .then()
                .assertThat().log().all().statusCode(200)
                .body("address", equalTo(newAddress))
                .extract().response().asString();

        System.out.println(response);
        JsonPath js1 = new JsonPath(getPlaceResponse);
        String actualAddress = js1.getString("address");
        System.out.println(actualAddress);

        //Assertion
        Assert.assertEquals(actualAddress, newAddress);
    }
}
