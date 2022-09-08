package com.fab.crud.example;

import com.fab.crud.example.repository.ProductRepository;
import com.fab.crud.example.entity.Product;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringBootCrudExample2ApplicationTests {

    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";

    private static RestTemplate restTemplate;

    @Autowired
    private TestH2Repository h2Repository;

    @Autowired
    private ProductRepository productRepository;


    public void initialize() {

        productRepository.save(new Product("Prduct1",20 , 409.99 ));
        productRepository.save(new Product( "Prduct2",56 , 433 ));
        productRepository.save(new Product( "Prduct3",34 , 980 ));
        productRepository.save(new Product( "Prduct4",29 , 893));

    }


    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();

    }

    @AfterAll
    public static void reset(){
       // productRepository.deleteAll();
    }


    @AfterEach
    public void resetdb(){
        productRepository.truncateTable();
        //productRepository.truncateHibernateSequenceTable();
    }

    @BeforeEach
    public void setUp() {
        baseUrl = baseUrl.concat(":").concat(port + "").concat("/products");
        initialize();
    }


    @Test
    void testAddProduct() {
       // initialize();
        Product product = new Product("headset", 2, 7999);
        List<Product> products =productRepository.findAll();
        Product response = restTemplate.postForObject(baseUrl, product, Product.class);
        //assertEquals(5, response.getId());
        assertEquals("headset", response.getName());
        assertEquals(5, productRepository.findAll().size()); //c 1 to 5

       // productRepository.truncateTable();
    }

    @Test
    //@Sql(statements = "INSERT INTO PRODUCT_TBL (id,name, quantity, price) VALUES (4,'AC', 1, 34000)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    //@Sql(statements = "DELETE FROM PRODUCT_TBL WHERE name='AC'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testGetProducts() {
        //initialize();
        //List<Product> product1s =productRepository.findAll();
        List<Product> products = restTemplate.getForObject(baseUrl, List.class);
        assertNotNull(products);
        assertEquals(4, products.size());
        //assertEquals(5, h2Repository.findAll().size());

       // productRepository.truncateTable();
    }

    @Test
   // @Sql(statements = "INSERT INTO PRODUCT_TBL (id,name, quantity, price) VALUES (10,'CAR', 1, 334000)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
   // @Sql(statements = "DELETE FROM PRODUCT_TBL WHERE id=10", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testFindProductById() {
        //initialize();
        List<Product> products =productRepository.findAll();

        Product product = restTemplate.getForObject(baseUrl + "/{id}", Product.class, products.get(0).getId()); // c 10 to 11
        assertAll(
                () -> assertNotNull(product),
                () -> assertEquals(products.get(0).getId(), product.getId()),
                () -> assertEquals("Prduct1", product.getName())
        );
      // productRepository.truncateTable();
    }

    @Test
    //@Sql(statements = "INSERT INTO PRODUCT_TBL (id,name, quantity, price) VALUES (2,'shoes', 1, 999)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    //@Sql(statements = "DELETE FROM PRODUCT_TBL WHERE id=2", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testUpdateProduct(){
        //initialize();
        Product product = new Product("shoes", 1, 1999);
        List<Product> products =productRepository.findAll();
                restTemplate.put(baseUrl+"/update/{id}", product, products.get(0).getId());
        Product productFromDB = productRepository.findByName("shoes");
        assertAll(
                () -> assertNotNull(productFromDB),
                () -> assertEquals(products.get(0).getId(), productFromDB.getId()),
                () -> assertEquals(1999, productFromDB.getPrice())
        );

        //productRepository.truncateTable();
    }

    @Test
    //@Sql(statements = "DELETE FROM PRODUCT_TBL", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    //@Sql(statements = "INSERT INTO PRODUCT_TBL (id,name, quantity, price) VALUES (8,'books', 5, 1499)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testDeleteProduct(){

        List<Product> products = productRepository.findAll();
        int recordCount=products.size();
        assertEquals(4, recordCount);
        restTemplate.delete(baseUrl+"/delete/{id}", products.get(0).getId());
        assertEquals(3, productRepository.findAll().size());

    }

}
