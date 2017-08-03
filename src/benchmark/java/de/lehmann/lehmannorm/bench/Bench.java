package de.lehmann.lehmannorm.bench;

import java.util.regex.Pattern;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@State(Scope.Benchmark)
public class Bench {

    @Setup
    public void setUp() {
        
    }
    
    private String input = "anything==possible";
    private Pattern pattern = Pattern.compile("==");
    
    @Benchmark
    public String[] benchOne() {
        
        return input.split("==", 2);
    }
    
    @Benchmark
    public String[] benchTwo() {
        
        return pattern.split(input, 2);
    }
    
    @TearDown
    public void tearDown() {
        
    }
}
