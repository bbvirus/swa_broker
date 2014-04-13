import HelloApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

import java.util.Properties;

class HelloImpl extends HelloPOA{
  private ORB orb;

  public void setORB(ORB orb_val){
    orb = orb_val;
  }
  
  public String sayHello(){
    return "\nHello world !!\n";
  }
  
  public void shutdown(){
    orb.shutdown(false);
  }
}

public class HelloServer{

  public static void main(String args[]){
    try{
      // ORB를 생성하고
      // 리퀘스트를 처리하는 데 활용할 rootpoa에 대한 참조를 얻고, POAManager를 활성화시킨다.
      // ( POA는 객체에 대한 참조를 사용하는 리퀘스트와, 그 리퀘스트에 대한 알맞은 서비스를 연결하는 방법을 제공한다 )
      ORB orb = ORB.init(args, null);
      POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
      rootpoa.the_POAManager().activate();

      // HelloPOA 추상 클래스를 상속받은 helloImpl 인스턴스를 만들고
      // ORB를 넣어둔다.
      // HelloPOA에는 InvokeHandler와 HelloOperations가 implements 되어있다.
      HelloImpl helloImpl = new HelloImpl();
      helloImpl.setORB(orb); 

      // HelloPOATie 객체의 인스턴스인 tie를 생성하는데
      // 앞에서 생성한 helloImpl과 rootpoa를 생성자에 전달한다.
      // 그러면 tie는 ORB와 POA를 가지고 있게 된다.
      HelloPOATie tie = new HelloPOATie(helloImpl, rootpoa);

      // tie의 _this메서드를 실행하면 HelloHelper의 narrow 메서드가 호출된다.
      // narrow 메서드의 반환값은 _HelloStub 클래스의 인스턴스이다.
      // _HelloStub 클래스는 Hello 인터페이스를 구현하고 있으므로 href에 저장할 수 있다.
      Hello href = tie._this(orb);
           
      // get the root naming context
      org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
      
      // Use NamingContextExt which is part of the Interoperable
      // Naming Service specification.
      NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

      // "Hello"라는 이름을 가지고 알맞은 서비스를 찾아 바인딩해둔다.
      String name = "Hello";
      NameComponent path[] = ncRef.to_name( name );
      ncRef.rebind(path, href);

      System.out.println("HelloServer ready and waiting ...");

      // wait for invocations from clients
      orb.run();
      } 
      
    catch (Exception e){
      System.err.println("ERROR: " + e);
      e.printStackTrace(System.out);
    }
    
    System.out.println("HelloServer Exiting ...");
        
  }
}
