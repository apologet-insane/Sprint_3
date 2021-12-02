
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CourierLoginTest {

    String courierLogin = RandomStringUtils.randomAlphabetic(10);
    String courierPassword = RandomStringUtils.randomAlphabetic(10);
    String courierFirstName = RandomStringUtils.randomAlphabetic(10);

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    @DisplayName("Курьер может авторизоваться, успешная авторизация возвращает id")
    public void courierLogin() {

        //создаём тестового курьера

        String registerRequestBody = "{\"login\":\"" + courierLogin + "\","
                    + "\"password\":\"" + courierPassword + "\","
                    + "\"firstName\":\"" + courierFirstName + "\"}";
                     given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(registerRequestBody)
                    .when()
                    .post("/api/v1/courier")
                    .then().assertThat().statusCode(201);

       //логинимся тестовым курьером
          String loginRequestBody = "{\"login\":\"" + courierLogin + "\","
                + "\"password\":\"" + courierPassword + "\"}";

            int id = given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(loginRequestBody)
                    .when()
                    .post("/api/v1/courier/login")
                    .then().assertThat().statusCode(200).and().extract().path("id");

            assertThat(id, notNullValue());

            //удаляем тестового курьера
            String deleteCourier = "/api/v1/courier/"+id;
                      given()
                     .delete(deleteCourier);
        }

    @Test
    @DisplayName("Курьер не может авторизоваться без передачи обязательных полей")
    public void courierLoginRequiredFields() {
        //создаём тестового курьера
        String registerRequestBody = "{\"login\":\"" + courierLogin + "\","
                + "\"password\":\"" + courierPassword + "\","
                + "\"firstName\":\"" + courierFirstName + "\"}";
                given()
                .header("Content-type", "application/json")
                .and()
                .body(registerRequestBody)
                .when()
                .post("/api/v1/courier")
                .then().assertThat().statusCode(201);

        //логинимся тестовым курьером без логина
        String loginRequestBodyWithoutLogin = "{\"login\":\"" + "\","
                + "\"password\":\"" + courierPassword + "\"}";

        String messageWithoutLogin = given()
                .header("Content-type", "application/json")
                .and()
                .body(loginRequestBodyWithoutLogin)
                .when()
                .post("/api/v1/courier/login")
                .then().assertThat().statusCode(400).and().extract().path("message");

        assertThat(messageWithoutLogin, equalTo("Недостаточно данных для входа"));

        //логинимся курьером без пароля
        String loginRequestBodyWithoutPassword = "{\"login\":\"" + courierLogin + "\","
                + "\"password\":\"" +  "\"}";

        String messageWithoutPassword = given()
                .header("Content-type", "application/json")
                .and()
                .body(loginRequestBodyWithoutPassword)
                .when()
                .post("/api/v1/courier/login")
                .then().assertThat().statusCode(400).and().extract().path("message");

        assertThat(messageWithoutPassword, equalTo("Недостаточно данных для входа"));

        //логинимся, чтобы получить id для удаления тестового курьера
        String loginRequestBodyForDelete = "{\"login\":\"" + courierLogin + "\","
                + "\"password\":\"" + courierPassword + "\"}";

        int id = given()
                .header("Content-type", "application/json")
                .and()
                .body(loginRequestBodyForDelete)
                .when()
                .post("/api/v1/courier/login")
                .then().assertThat().statusCode(200).and().extract().path("id");
        //удаляем тестового курьера
        String deleteCourier = "/api/v1/courier/"+id;
                 given()
                .delete(deleteCourier);
    }
    @Test
    @DisplayName("Если пароль или логин неверный, запрос вернёт ошибку")
    public void courierLoginWithBadData() {
        //создаём тестового курьера
        String registerRequestBody = "{\"login\":\"" + courierLogin + "\","
                + "\"password\":\"" + courierPassword + "\","
                + "\"firstName\":\"" + courierFirstName + "\"}";
        given()
                .header("Content-type", "application/json")
                .and()
                .body(registerRequestBody)
                .when()
                .post("/api/v1/courier")
                .then().assertThat().statusCode(201);

        //логинимся тестовым курьером с неправильным логином
        String loginRequestBodyWithBadLogin = "{\"login\":\"" + courierLogin + courierLogin + "\","
                + "\"password\":\"" + courierPassword + "\"}";

        String messageWithBadLogin = given()
                .header("Content-type", "application/json")
                .and()
                .body(loginRequestBodyWithBadLogin)
                .when()
                .post("/api/v1/courier/login")
                .then().assertThat().statusCode(404).and().extract().path("message");

        assertThat(messageWithBadLogin, equalTo("Учетная запись не найдена"));

        //логинимся курьером с неправильным паролем
        String loginRequestBodyWithBadPassword = "{\"login\":\"" + courierLogin + "\","
                + "\"password\":\"" + courierPassword + courierPassword + "\"}";

        String messageWithBadPassword = given()
                .header("Content-type", "application/json")
                .and()
                .body(loginRequestBodyWithBadPassword)
                .when()
                .post("/api/v1/courier/login")
                .then().assertThat().statusCode(404).and().extract().path("message");

        assertThat(messageWithBadPassword, equalTo("Учетная запись не найдена"));

        //логинимся курьером с неправильным логином и паролем
        String loginRequestBodyWithBadLoginPassword = "{\"login\":\"" + courierLogin + courierPassword + "\","
                + "\"password\":\"" + courierPassword + courierLogin + "\"}";

                String messageWithBadLoginPassword = given()
                .header("Content-type", "application/json")
                .and()
                .body(loginRequestBodyWithBadLoginPassword)
                .when()
                .post("/api/v1/courier/login")
                .then().assertThat().statusCode(404).and().extract().path("message");

        assertThat(messageWithBadPassword, equalTo("Учетная запись не найдена"));


        //логинимся, чтобы получить id для удаления тестового курьера
        String loginRequestBodyForDelete = "{\"login\":\"" + courierLogin + "\","
                + "\"password\":\"" + courierPassword + "\"}";

        int id = given()
                .header("Content-type", "application/json")
                .and()
                .body(loginRequestBodyForDelete)
                .when()
                .post("/api/v1/courier/login")
                .then().assertThat().statusCode(200).and().extract().path("id");
        //удаляем тестового курьера
        String deleteCourier = "/api/v1/courier/"+id;
        given()
                .delete(deleteCourier);
    }
@Test
@DisplayName("Логин курьером, которого не существует")
   public void courierUnknownLogin() {

       String loginRequestBodyNonExistentUser = "{\"login\":\"" + courierLogin + courierPassword + "\","
               + "\"password\":\"" + courierPassword + courierLogin + "\"}";

       String message = given()
               .header("Content-type", "application/json")
               .and()
               .body(loginRequestBodyNonExistentUser)
               .when()
               .post("/api/v1/courier/login")
               .then().assertThat().statusCode(404).and().extract().path("message");

       assertThat(message, equalTo("Учетная запись не найдена"));
   }
}
