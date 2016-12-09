package thrift;

import com.facebook.swift.codec.ThriftField.Requiredness;
import com.google.common.util.concurrent.ListenableFuture;

@ThriftService("HelloWorldService")
public interface HelloWorldService {
    @ThriftMethod(value = "sayHello")
    String sayHello(
            @ThriftField(value = 1, name = "username", requiredness = Requiredness.NONE) final String username,
            @ThriftField(value = 2, name = "Hello", requiredness = Requiredness.NONE) final Hello hello
    );

    @ThriftMethod(value = "sayHello2")
    void sayHello2(
            @ThriftField(value = 1, name = "username", requiredness = Requiredness.NONE) final String username,
            @ThriftField(value = 2, name = "Hello", requiredness = Requiredness.NONE) final Hello hello
    );

    @ThriftMethod(value = "say",
            oneway = true)
    void say(
            @ThriftField(value = 1, name = "message", requiredness = Requiredness.NONE) final String message
    );


    @ThriftService("HelloWorldService")
    public interface Async {
        @ThriftMethod(value = "sayHello")
        ListenableFuture<String> sayHello(
                @ThriftField(value = 1, name = "username", requiredness = Requiredness.NONE) final String username,
                @ThriftField(value = 2, name = "Hello", requiredness = Requiredness.NONE) final Hello hello
        );

        @ThriftMethod(value = "sayHello2")
        ListenableFuture<Void> sayHello2(
                @ThriftField(value = 1, name = "username", requiredness = Requiredness.NONE) final String username,
                @ThriftField(value = 2, name = "Hello", requiredness = Requiredness.NONE) final Hello hello
        );

        @ThriftMethod(value = "say",
                oneway = true)
        ListenableFuture<Void> say(
                @ThriftField(value = 1, name = "message", requiredness = Requiredness.NONE) final String message
        );
    }

}