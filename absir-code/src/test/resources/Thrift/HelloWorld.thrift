namespace java thrift
namespace csharp thrift
namespace java.swift thrift

struct Hello {
	1: i32 id = 1;
	2: i64 id2 = 2;
}

struct Hello2 {
	1: i32 id = 1;
	2: i64 id2 = 2;
}

service HelloWorldService {
    string sayHello(1:string username, 2:Hello Hello)

    void sayHello2(1:string username, 2:Hello Hello)

    oneway void say(1:string message);

}