package me.selim.mesh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MeshNetworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeshNetworkApplication.class, args);
    }

}
