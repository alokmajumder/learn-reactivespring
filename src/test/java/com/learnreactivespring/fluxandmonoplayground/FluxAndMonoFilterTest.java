package com.learnreactivespring.fluxandmonoplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
public class FluxAndMonoFilterTest {
	
	List<String> names = Arrays.asList("adam","anna","jack","jenny");
	
	@Test
    public void filterTest() {
		
		Flux<String> namesFlux = Flux.fromIterable(names)
				.filter(p-> p.startsWith("a"))
				.log();
		
		StepVerifier.create(namesFlux)
        .expectNext("adam","anna")
        .verifyComplete();
	}
	
	@Test
    public void filterTestLength() {
		
		Flux<String> namesFlux = Flux.fromIterable(names)
				.filter(p-> p.length() > 4)
				.log();
		
		StepVerifier.create(namesFlux)
        .expectNext("jenny")
        .verifyComplete();
	
	}

}
