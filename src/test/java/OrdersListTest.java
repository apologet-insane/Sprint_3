import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertFalse;

public class OrdersListTest {

   //задаём рандомные (кроме цвета) переменные для тестового заказа
    String firstName = RandomStringUtils.randomAlphabetic(10);
    String lastName = RandomStringUtils.randomAlphabetic(10);
    String address = RandomStringUtils.randomAlphabetic(10) + ", " + RandomStringUtils.randomNumeric(2);
    String metroStation =  ""+ RandomUtils.nextInt(1, 20) + "";
    String phone = "+7 " + RandomStringUtils.randomNumeric(10);
    String rentTime = ""+ RandomUtils.nextInt(1, 9) + "";
    String deliveryDate = RandomUtils.nextInt(2021, 2029)+"-"+"0" + RandomUtils.nextInt(1, 9)+ "-"+"0"+RandomUtils.nextInt(1, 9);
    String comment = RandomStringUtils.randomAlphabetic(10);
    String color = "[" + "\"BLACK" + "\"" + "]";

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    @DisplayName("Получаем список заказов")
    public void getOrdersList() {
        checkOrders(orders());

    }

    @Step("Создаём тестовый заказ, чтобы список был гарантировано не пустым")
    public void createTestOrder(){
        String orderBody = "{\"firstName\":\"" + firstName + "\","
                + "\"lastName\":\"" + lastName + "\","
                + "\"address\":\"" + address + "\","
                + "\"metroStation\":" + metroStation + ","
                + "\"phone\":\"" + phone + "\","
                + "\"rentTime\":" + rentTime + ","
                + "\"deliveryDate\":\"" + deliveryDate + "\","
                + "\"comment\":\"" + comment + "\","
                + "\"color\":" + color + "}";

                given()
                .header("Content-type", "application/json")
                .and()
                .body(orderBody)
                .when()
                .post("/api/v1/orders")
                .then().assertThat().statusCode(201)
                .and().extract().path("track");
    }

    @Step
    public List<Object> orders () {
    //получаем список заказов и сохраняем в orders
    List<Object> orders = given()
            .header("Content-type", "application/json")
            .when()
            .get("/api/v1/orders").then().assertThat().statusCode(200)
            .and().extract().jsonPath().getList("orders");
    return orders;
}
    @Step("Проверяем, что список не пустой")
    public void checkOrders(List<Object> orders){

        assertFalse(orders.isEmpty());

    }



    }


