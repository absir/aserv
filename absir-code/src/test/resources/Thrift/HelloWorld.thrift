namespace java thrift
namespace csharp thrift

struct Hello {
	1: i32 id = -1;
	2: i64 id2 = -1;
	3: i64 id3 = -1;
	4: i64 id4 = -1;
	5: i64 id5 = -1;
}

struct Hello2 {
	1: optional i32 id = 1;
	2: optional i64 id2 = 2;
}

service HelloWorldService {
    string sayHello(1:string username, 2:Hello Hello)

    void sayHello2(1:string username, 2:Hello Hello)

    oneway void say(1:string message);

}