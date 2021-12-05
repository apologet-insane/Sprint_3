import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {
   //задаём рандомные переменные для json, который станет телом запроса при создании заказа

    String firstName = RandomStringUtils.randomAlphabetic(10);
    String lastName = RandomStringUtils.randomAlphabetic(10);
    String address = RandomStringUtils.randomAlphabetic(10) + ", "+ RandomStringUtils.randomNumeric(2);
    String metroStation =  ""+ RandomUtils.nextInt(1, 20) + "";
    String phone = "+7 "+ RandomStringUtils.randomNumeric(10);
    String rentTime = ""+ RandomUtils.nextInt(1, 9) + "";
    String deliveryDate = RandomUtils.nextInt(2021, 2029)+"-"+"0" + RandomUtils.nextInt(1, 9)+ "-"+"0"+RandomUtils.nextInt(1, 9);
    String comment = RandomStringUtils.randomAlphabetic(10);
    String color;

    @Before
    public void setUp() {

         RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    public CreateOrderTest(String color){
        this.color = color;
    }

    //задаём параметры для color
    @Parameterized.Parameters
    public static Object[] getData() {
        return new Object[][]{
                {"["+"\"BLACK"+"\""+"]"},
                {"["+"\"GREY"+"\""+"]"},
                {"["+"\"BLACK"+"\""+ ", "  +"\"GREY"+"\""+"]"}
        };
    }

    @Test
    @DisplayName("Формируем заказ, проверяем, что ответ не null")
    public void orderColorTest() {
        Response response = orderRequest(orderBody());
        checkOrderResponse(response);
    }

    @Step ("Формируем тело запроса")
    public String orderBody() {
            String bodyOrder = "{\"firstName\":\"" + firstName + "\","
                    + "\"lastName\":\"" + lastName + "\","
                    + "\"address\":\"" + address + "\","
                    + "\"metroStation\":" + metroStation + ","
                    + "\"phone\":\"" + phone + "\","
                    + "\"rentTime\":" + rentTime + ","
                    + "\"deliveryDate\":\"" + deliveryDate + "\","
                    + "\"comment\":\"" + comment + "\","
                    + "\"color\":" + color + "}";
            return bodyOrder;
    }

    @Step ("Формируем тестовый запрос")
    public Response orderRequest(String body){
              Response response =   given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/api/v1/orders");

              return response;
    }
    @Step ("Проверяем тестовый запрос")
    public void checkOrderResponse(Response response){
        response.then().assertThat().statusCode(201)
                .and().assertThat().body("track", notNullValue());
    }
}
