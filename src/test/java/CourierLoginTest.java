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

public class CourierLoginTest {

   final static String courierLogin = RandomStringUtils.randomAlphabetic(10);
   final static  String courierPassword = RandomStringUtils.randomAlphabetic(10);
   final static String courierFirstName = RandomStringUtils.randomAlphabetic(10);


    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";

   }

    @Test
    @DisplayName("Курьер может авторизоваться, успешная авторизация возвращает id")
    public void courierLogin() {

       Response response = loginCourierPositive(createTestCourier());
       checkResponseCourierLogin(response);
   }

   @Step("Создаём тестового курьера")
   public String createTestCourier() {
       String registerRequestBody = "{\"login\":\"" + courierLogin + "\","
               + "\"password\":\"" + courierPassword + "\","
               + "\"firstName\":\"" + courierFirstName + "\"}";
               given()
                .header("Content-type", "application/json")
                .and()
                .body(registerRequestBody)
                .when()
                .post("/api/v1/courier");

               String testLoginBody = "{\"login\":\"" + courierLogin + "\","
               + "\"password\":\"" + courierPassword + "\"}";

               return testLoginBody;
    }

   @Step ("Позитивная авторизация")
    public Response loginCourierPositive(String testCourier){

     Response response = given()
        .header("Content-type", "application/json")
        .and()
        .body(testCourier)
        .when()
        .post("/api/v1/courier/login");

        return response;
    }

    @Step ("Проверка id и статуса")
    public void checkResponseCourierLogin (Response response){
        int id =  response.then().assertThat().statusCode(200).and().extract().path("id");

        assertThat(id, notNullValue());
    }

    @Test
    @DisplayName("Курьер не может авторизоваться без передачи логина")
    public void testLoginWithoutLogin() {

        Response response = loginWithoutLogin(requestBodyLoginWithoutLogin());
        checkResponseLoginWithoutLogin(response);

    }

    @Step ("Тело реквеста для авторизации без логина")
    public String requestBodyLoginWithoutLogin(){

        String loginRequestBodyWithoutLogin = "{\"password\":\"" + courierPassword + "\"}";

        return loginRequestBodyWithoutLogin;
    }

    @Step ("Авторизация без логина")
    public Response loginWithoutLogin(String body) {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/api/v1/courier/login");

        return response;
    }

    @Step ("Проверка респонса авторизации без логина")
    public void checkResponseLoginWithoutLogin (Response response){

        String messageWithoutLogin = response.then().assertThat()
                .statusCode(400).and().extract().path("message");

        assertThat(messageWithoutLogin, equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Курьер не может авторизоваться без передачи пароля")
    public void testLoginWithoutPassword(){

        Response response = loginWithoutPassword(requestBodyLoginWithoutPassword());

        checkResponseLoginWithoutPassword(response);
        }

    @Step ("Тело реквеста для авторизации без пароля")
    public String requestBodyLoginWithoutPassword(){

        String loginRequestBodyWithoutPassword = "{\"login\":\"" + courierLogin +  "\"}";

        return loginRequestBodyWithoutPassword;
    }

    @Step ("Авторизация без пароля")
    public Response loginWithoutPassword(String body){

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/api/v1/courier/login");

        return response;
    }
    @Step ("Проверка респонса авторизации без пароля")
    public void checkResponseLoginWithoutPassword (Response response) {
        String messageWithoutPassword = response.then()
                .assertThat().statusCode(400).and().extract().path("message");

        assertThat(messageWithoutPassword, equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Если логин неверный, запрос вернёт ошибку")
    public void testLoginWithBadLogin() {

        Response response = loginWithBadLogin(requestBodyLoginWithBadLogin());
        checkResponseLoginWithBadLogin(response);
    }

    @Step ("Тело реквеста для авторизации c неправильным логином")
    public String requestBodyLoginWithBadLogin(){

        String loginRequestBodyWithBadLogin  = "{\"login\":\"" + courierLogin + courierLogin + "\","
                + "\"password\":\"" + courierPassword + "\"}";

        return loginRequestBodyWithBadLogin;
    }

    @Step("Авторизация с неправильным логином")
    public Response loginWithBadLogin(String body) {

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/api/v1/courier/login");

        return response;
    }
    @Step ("Проверка респонса авторизации с неправильным логином")
    public void checkResponseLoginWithBadLogin (Response response) {

        String messageWithBadLogin = response.then().assertThat()
                .statusCode(404).and().extract().path("message");

        assertThat(messageWithBadLogin, equalTo("Учетная запись не найдена"));
    }
    @Test
    @DisplayName("Если пароль неверный, запрос вернёт ошибку")
    public void testLoginWithBadPassword() {

        Response response = loginWithBadPassword(requestBodyLoginWithBadPassword());
        checkResponseLoginWithBadPassword(response);

    }

    @Step ("Тело реквеста для авторизации c неправильным паролем")
    public String requestBodyLoginWithBadPassword(){

        String loginRequestBodyWithBadPassword = "{\"login\":\"" + courierLogin + "\","
            + "\"password\":\"" + courierPassword + courierPassword + "\"}";

        return loginRequestBodyWithBadPassword;
    }

   @Step("Авторизация с неверным паролем")
   public Response loginWithBadPassword(String body) {
           Response response = given()
                   .header("Content-type", "application/json")
                   .and()
                   .body(body)
                   .when()
                   .post("/api/v1/courier/login");

           return response;

    }
    @Step ("Проверка респонса авторизации с неправильным паролем")
    public void checkResponseLoginWithBadPassword (Response response) {
        String messageWithBadPassword = response.then().assertThat().statusCode(404).and().extract().path("message");

        assertThat(messageWithBadPassword, equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Если пароль и логин неверный, запрос вернёт ошибку (несуществующий пользователь)")
    public void testLoginWithBadLoginPassword() {

        Response response = loginWithBadLoginPassword(requestBodyLoginWithBadLoginPassword());
        checkResponseLoginWithBadLoginPassword(response);

    }

    @Step ("Тело реквеста для авторизации c неправильным логином и паролем +" +
            "т.е. несуществующим пользователем")
    public String requestBodyLoginWithBadLoginPassword(){

        String loginRequestBodyWithBadLoginPassword = "{\"login\":\"" + courierLogin + courierPassword + "\","
                + "\"password\":\"" + courierPassword + courierLogin + "\"}";

        return loginRequestBodyWithBadLoginPassword;
    }

    @Step("Авторизация с неверным логином и паролем")
    public Response loginWithBadLoginPassword(String body) {

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/api/v1/courier/login");

        return response;
    }

    @Step ("Проверка респонса авторизации с неверным паролем и логином (несуществующий пользователь)")
    public void checkResponseLoginWithBadLoginPassword (Response response) {
        String messageWithBadLoginPassword = response.then().assertThat().statusCode(404).and().extract().path("message");

        assertThat(messageWithBadLoginPassword, equalTo("Учетная запись не найдена"));
    }

    @Step ("Получаем id для удаления")
    public int idForDelete (){
    //логинимся, чтобы получить id для удаления тестового курьера
    String loginRequestBodyForDelete = "{\"login\":\"" + courierLogin + "\","
            + "\"password\":\"" + courierPassword + "\"}";
    int id = given()
            .header("Content-type", "application/json")
            .and()
            .body(loginRequestBodyForDelete)
            .when()
            .post("/api/v1/courier/login")
            .then().extract().path("id");
    return id;

}

    @Step ("Удаляем тестового курьера")
    //При помещение в After весь тест падает, не успел разобраться, почему
    public void deleteTestCourier (){
        //удаляем тестового курьера
        String deleteCourier = "/api/v1/courier/"+idForDelete();
        given().delete(deleteCourier);

    }
}

