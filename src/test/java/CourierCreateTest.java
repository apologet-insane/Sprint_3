
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

public class CourierCreateTest {
    // с помощью библиотеки RandomStringUtils генерируем логин
    // метод randomAlphabetic генерирует строку, состоящую только из букв, в качестве параметра передаём длину строки
    String courierLogin = RandomStringUtils.randomAlphabetic(10);
    // с помощью библиотеки RandomStringUtils генерируем пароль
    String courierPassword = RandomStringUtils.randomAlphabetic(10);
    // с помощью библиотеки RandomStringUtils генерируем имя курьера
    String courierFirstName = RandomStringUtils.randomAlphabetic(10);

    // собираем в строку тело запроса на регистрацию, подставляя в него логин, пароль и имя курьера


    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";


        }
    @Test
    @DisplayName("Проверка создания курьера: код ответа, ok:true")
    public void createCourierPosistive(){

        String registerRequestBody = "{\"login\":\"" + courierLogin + "\","
                + "\"password\":\"" + courierPassword + "\","
                + "\"firstName\":\"" + courierFirstName + "\"}";

                   boolean ok =  given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(registerRequestBody)
                    .when()
                    .post("https://qa-scooter.praktikum-services.ru/api/v1/courier")
                    .then().assertThat().statusCode(201).and().extract().path("ok");

                   assertTrue(ok);
    }
    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров с одинаковыми логинами")
    public void createTwoSameCouriers() {

       String registerRequestBody =
               "{\"login\":\"" + courierLogin + "\","
                + "\"password\":\"" + courierPassword + "\","
                + "\"firstName\":\"" + courierFirstName + "\"}";

                given()
                .header("Content-type", "application/json")
                .and()
                .body(registerRequestBody)
                .when()
                .post("https://qa-scooter.praktikum-services.ru/api/v1/courier")
                .then().assertThat().statusCode(201);

        String message = given()
                .header("Content-type", "application/json")
                .and()
                .body(registerRequestBody)
                .when()
                .post("https://qa-scooter.praktikum-services.ru/api/v1/courier")
                .then().assertThat().statusCode(409).and().extract().path("message");

        assertThat(message, equalTo("Этот логин уже используется. Попробуйте другой."));
    }
    @Test
    @DisplayName("Чтобы создать курьера, нужно передать в ручку все обязательные поля: если нет одного из обязательных полей, возвращается ошибка")
    public void createWithoutMandatoryField() {

        String registerRequestBodyWithoutLogin = "{\"password\":\"" + courierPassword + "\","
                + "\"firstName\":\"" + courierFirstName + "\"}";

               String messageWithoutLogin = given()
                .header("Content-type", "application/json")
                .and()
                .body(registerRequestBodyWithoutLogin)
                .when()
                .post("https://qa-scooter.praktikum-services.ru/api/v1/courier")
                .then().assertThat().statusCode(400).and().extract()
                .path("message");

        String registerRequestBodyWithoutPassword = "{\"login\":\"" + courierLogin + "\","
               + "\"firstName\":\"" + courierFirstName + "\"}";

     String messageWithoutPassword =  given()
                .header("Content-type", "application/json")
                .and()
                .body(registerRequestBodyWithoutPassword)
                .when()
                .post("https://qa-scooter.praktikum-services.ru/api/v1/courier")
                .then().assertThat().statusCode(400).and().extract()
                .path("message");

              assertThat(messageWithoutLogin, equalTo("Недостаточно данных для создания учетной записи"));
              assertThat (messageWithoutPassword, equalTo("Недостаточно данных для создания учетной записи"));
    }
}

