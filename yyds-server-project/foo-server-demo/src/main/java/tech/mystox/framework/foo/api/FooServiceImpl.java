package tech.mystox.framework.foo.api;

import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/9/9, 13:03.
 * company: mystox
 * description:
 * update record:
 */
@Service
public class FooServiceImpl implements FooService {
    @Override
    public void foo(String foo) {
        System.out.println(foo);
    }

    @Override
    public String boo(String msg) {
        System.out.println("============" + msg);
        return "result";
      /*  JSONObject jsonObject = JSONObject.parseObject(foo);
        Integer a = jsonObject.getInteger("a");
        String s = "你我他";
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < 8 * 1024; i++) {
            buffer.append(s + i);
        }
        int length = buffer.length();
        System.out.println(length);
        System.out.println(buffer.toString().getBytes(Charset.defaultCharset()).length / 1024);


        return buffer.toString();*/
    }

    @Override
    public String coo(String coo) {
        System.out.println("执行coo" + coo);
        try {
            Thread.sleep(Long.valueOf(coo));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end" + coo);
        return "end";
    }

    @Override
    public String packageSum(String poo) {
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < Integer.valueOf(poo); i++)
            s.append("i");
        return s.toString();
    }
}
