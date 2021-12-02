import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
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
    String metroStation = RandomStringUtils.randomNumeric(1);
    String phone = "+7 "+ RandomStringUtils.randomNumeric(10);
    String rentTime = RandomStringUtils.randomNumeric(1);
    String deliveryDate = RandomStringUtils.randomNumeric(4)+"-"+"0" + RandomStringUtils.randomNumeric(1)+ "-"+"0"+RandomStringUtils.randomNumeric(1);
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

        String orderBody = "{\"firstName\":\"" + firstName + "\","
                + "\"lastName\":\"" + lastName + "\","
                + "\"address\":\"" + address + "\","
                + "\"metroStation\":" + metroStation + ","
                + "\"phone\":\"" + phone + "\","
                + "\"rentTime\":" + rentTime + ","
                + "\"deliveryDate\":\"" + deliveryDate + "\","
                + "\"comment\":\"" + comment +"\","
                + "\"color\":" + color + "}";

                 given()
                .header("Content-type", "application/json")
                .and()
                .body(orderBody)
                .when()
                .post("/api/v1/orders")
                .then().assertThat().statusCode(201)
                .and().assertThat().body("track", notNullValue());
    }
}
