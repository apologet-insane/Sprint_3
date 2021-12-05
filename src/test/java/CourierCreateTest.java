import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class CourierCreateTest {
    String courierLogin = RandomStringUtils.randomAlphabetic(10);
    String courierPassword = RandomStringUtils.randomAlphabetic(10);
    String courierFirstName = RandomStringUtils.randomAlphabetic(10);

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    @DisplayName("Проверка создания курьера: код ответа и тело: ok:true")
    public void createCourierPosistive(){
        Response response = createCourierPositive(registerRequestBody());
        checkResponsePositveCreateCourier(response);
    }

    @Step ("Позитивный запрос на создание курьера")
        public Response createCourierPositive (String body){

        Response response = given()
              .header("Content-type", "application/json")
              .and()
              .body(body)
              .when()
              .post("/api/v1/courier");

        return response;
    }

    @Step("Получить JSON для тела реквеста")
    public String registerRequestBody(){

        String registerRequestBody = "{\"login\":\"" + courierLogin + "\","
                + "\"password\":\"" + courierPassword + "\","
                + "\"firstName\":\"" + courierFirstName + "\"}";
        return registerRequestBody;
    }

    @Step("Проверка позитивного респонса на создание курьера")
    public void checkResponsePositveCreateCourier(Response response) {
        response.then().assertThat().statusCode(201);
        response.then().assertThat().body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров с одинаковыми логинами")
    public void testTwoSameCouriers() {
        Response response = createTwoSameCouriers(registerRequestBody());
        checkResponseCreateSameCouriers(response);

    }

    @Step("Создание двух одинаковых курьеров")
     public Response createTwoSameCouriers(String body){

                 given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/api/v1/courier");

       Response response =  given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/api/v1/courier");
       return response;
    }

    @Step("Проверка респонса на создание курьера с данными уже существующего курьера")
    public void checkResponseCreateSameCouriers(Response response) {
        response.then().assertThat().statusCode(409);
        response.then().assertThat().
                body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Чтобы создать курьера, нужно передать в ручку все обязательные поля: " +
            "если нет логина, возвращается ошибка")
    public void testCreateCourierWithoutLogin() {

        Response response = createCourierWithoutLogin(registerRequestBodyWithoutLogin());
        checkResponseCreateCourierWithoutLogin(response);

    }

    @Step("Получить JSON для тела реквеста без логина")
    public String registerRequestBodyWithoutLogin(){

        String registerRequestBody = "{\"password\":\"" + courierPassword + "\","
                + "\"firstName\":\"" + courierFirstName + "\"}";
        return registerRequestBody;
    }

    @Step ("Создание курьера без логина")
    public Response createCourierWithoutLogin (String body){

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/api/v1/courier");

        return response;
    }
    @Step("Проверка респонса на создание курьера без передачи логина")
    public void checkResponseCreateCourierWithoutLogin(Response response) {
        String messageWithoutLogin = response.then().assertThat()
                .statusCode(400).and().extract()
                .path("message");

        assertThat(messageWithoutLogin, equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Чтобы создать курьера, нужно передать в ручку все обязательные поля: " +
            "если нет пароля, возвращается ошибка")
    public void testCreateCourierWithoutPassword(){

        Response response = createCourierWithoutPassword(registerRequestBodyWithoutPassword());
        checkResponseCreateCourierWithoutPassword(response);

    }

    @Step("Получить JSON для тела реквеста без пароля")
    public String registerRequestBodyWithoutPassword(){

        String registerRequestBody =  "{\"login\":\"" + courierLogin + "\","
                + "\"firstName\":\"" + courierFirstName + "\"}";
        return registerRequestBody;
    }

    @Step ("Создание курьера без пароля")
    public Response createCourierWithoutPassword (String body){

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/api/v1/courier");

        return response;
    }

    @Step("Проверка респонса на создание курьера без передачи пароля")
    public void checkResponseCreateCourierWithoutPassword(Response response) {
        String messageWithoutPassword =
                response.then().assertThat().statusCode(400).and().extract()
                        .path("message");

        assertThat(messageWithoutPassword, equalTo("Недостаточно данных для создания учетной записи"));
    }
}

