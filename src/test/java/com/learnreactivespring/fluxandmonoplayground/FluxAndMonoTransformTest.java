package com.learnreactivespring.fluxandmonoplayground;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

	List<String> names = Arrays.asList("adam","anna","jack","jenny");
	
	@Test
    public void transformUsingMap() {
		Flux<String> namesFlux = Flux.fromIterable(names)
				.map(p-> p.toUpperCase())
				.log();
		
		StepVerifier.create(namesFlux)
        .expectNext("ADAM", "ANNA", "JACK", "JENNY")
        .verifyComplete();
		
	}
	
	@Test
    public void transformUsingMap_Length() {
		Flux<Integer> namesFlux = Flux.fromIterable(names)
				.map(p-> p.length())
				.repeat(1)
				.log();
		
		StepVerifier.create(namesFlux)
        .expectNext(4,4,4,5,4,4,4,5)
        .verifyComplete();
		
	}
	
	@Test
    public void transformUsingMap_Filter() {
		Flux<String> namesFlux = Flux.fromIterable(names)
				.filter(p-> p.length() > 4)
				.map(p-> p.toUpperCase())
				.log();
		
		StepVerifier.create(namesFlux)
        .expectNext("JENNY")
        .verifyComplete();
		
	}
	
	@Test
    public void tranformUsingFlatMap(){

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F","G")) // A, B, C, D, E, F
                .flatMap(s -> {

                    return Flux.fromIterable(convertToList(s)); // A -> List[A, newValue] , B -> List[B, newValue]
                })//db or external service call that returns a flux -> s -> Flux<String>
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(14)
                .verifyComplete();
    }

	private List<String> convertToList(String s) {
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Arrays.asList(s, "newValue1");
	}
	
    @Test
    public void tranformUsingFlatMap_usingparallel(){

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F")) // Flux<String>
                .window(2) //Flux<Flux<String> -> (A,B), (C,D), (E,F)
                .flatMap((s) ->
                    s.map(this::convertToList).subscribeOn(parallel())) // Flux<List<String>
                    .flatMap(s -> Flux.fromIterable(s)) //Flux<String>
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }
    
    @Test
    public void tranformUsingFlatMap_parallel_maintain_order(){

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F")) // Flux<String>
                .window(2) //Flux<Flux<String> -> (A,B), (C,D), (E,F)
               /* .concatMap((s) ->
                        s.map(this::convertToList).`(parallel())) */// Flux<List<String>
                .flatMapSequential((s) ->
                        s.map(this::convertToList).subscribeOn(parallel()))
                .flatMap(s -> Flux.fromIterable(s)) //Flux<String>
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }
	
	
}
