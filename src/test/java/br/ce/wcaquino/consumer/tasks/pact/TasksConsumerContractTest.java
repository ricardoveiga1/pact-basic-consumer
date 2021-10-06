package br.ce.wcaquino.consumer.tasks.pact;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.PactProviderRule;
import au.com.dius.pact.consumer.junit.PactVerification;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import br.ce.wcaquino.consumer.tasks.model.Task;
import br.ce.wcaquino.consumer.tasks.service.TasksConsumer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

public class TasksConsumerContractTest {

    @Rule
    //lado do servidor, inteligencia para criar, this =esta instância
    public PactProviderRule mockProvider = new PactProviderRule("Tasks", this);
    //public PactProviderRule mockProvider = new PactProviderRule("Tasks", "localhost", 8080, this);

    @Pact(consumer = "BasicConsumer")//aquié uma api mockada, diferente do teste unitario
    public RequestResponsePact createPact(PactDslWithProvider builder){
        return builder
                .given("There is a task with id = 1") //Dado
                .uponReceiving("Retrieve Task #1")  //Quando
                    .path("/todo/1")
                    .method("GET")
                .willRespondWith()   //Então
                    .status(200)
                    .body("{\"id\":\"1\", \"task\":\"Task from pact\", \"dueDate\":\"2021-01-01\"}")
                .toPact();
    }
    @Test
    @PactVerification
    public void test() throws IOException {
        //Arrange
        TasksConsumer consumer = new TasksConsumer(mockProvider.getUrl()); //mockprovider que vai gerar a url no momento do teste, poderia por fixo, mas posso ter problema de porta em uso
        System.out.println(mockProvider.getUrl());

        //Act
        Task task = consumer.getTask(1L); //por break point para verificar a api criada durante o teste em modo debug
        System.out.println(task);

        //Assert
        Assert.assertThat(task.getId(), CoreMatchers.is(1L));
        Assert.assertThat(task.getTask(), CoreMatchers.is("Task from pact"));
        //a api ou url de teste só fica no ar durante a execução do teste, o pact que gerencia

    }
}
