import HelloApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;

public class HelloClient{

  public static void main(String args[]){
  
    try{
      // ORB를 생성하고
      // 이를 활용해 naming context를 활용하기 위한 준비 작업을 한다.
      ORB orb = ORB.init(args, null);
      org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
          
      // Use NamingContextExt instead of NamingContext. This is part of the Interoperable naming Service.
      NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
 
      // resolve the Object Reference in Naming
      // "Hello"라는 이름을 가지고 naming context를 활용해 서비스를 찾는다.
      // HelloHelper의 narrow 메서드를 호출하면
      // Hello 인터페이스를 구현한 _HelloStub의 인스턴스가 반환된다.
      // 반환된 인스턴스는 helloImpl에 저장된다.
      String name = "Hello";
      Hello helloImpl = HelloHelper.narrow(ncRef.resolve_str(name));

      // helloImpl의 sayHello()메서드는 _HelloStub.java의 sayHello()메서드이다.
      // _HelloStub에서 메서드 내용을 확인해보면
      // "sayHello"라는 이름을 활용해 알맞은 서비스를 찾고
      // 결과 String을 반환해준다. (아마도 "Hello World"일 것이다.)
      System.out.println("Obtained a handle on server object: " + helloImpl);
      System.out.println(helloImpl.sayHello());
      
      // helloImple에서 shutdown을 실행하면
      // 역시 _HelloStub.java의 shutdown 메서드가 실행된다.
      // "shutdown"이름을 가지고 리퀘스트를 처리할 서비스를 찾아 _invoke를 통해 실행시킨다.
      helloImpl.shutdown();
      }
      
    catch (Exception e) {
      System.out.println("ERROR : " + e) ;
      e.printStackTrace(System.out);
    }
  }
}