package com.github.taojoe.proto;

import com.github.taojoe.Transformer;
import com.google.protobuf.ByteString;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by joe on 4/19/16.
 */
public class TransformerTest {
    public enum LeveType{
        LV0,LV1,LV2;
    }
    public static class User{
        public String uid;
        public List<String> tags;
        public LocalDateTime login_time;
        public LeveType level_type;
        private boolean enabled1;
        public byte[] avatar;

        public boolean isEnabled() {
            return enabled1;
        }

        public void setEnabled(boolean enabled) {
            this.enabled1 = enabled;
        }
    }
    public static class Session{
        public User user;
        public List<User> friends;
        private Map<String, User> relations;

        public Map<String, User> getRelations() {
            return relations;
        }

        public void setRelations(Map<String, User> relations) {
            this.relations = relations;
        }
    }
    @Test
    public void testMessageToJava()  {
        Transformer trans=new Transformer();
        Transform.User.Builder user= Transform.User.newBuilder().setUid("uu").addTags("aa").addTags("bb")
                .setLoginTime(LocalDateTime.now().toString())
                .setLevelType(Transform.UserLevelType.LV0)
                .setAvatar(ByteString.copyFrom("abc".getBytes()))
                .setEnabled(true);
        Transform.SessionResponse.Builder session= Transform.SessionResponse.newBuilder();
        session.setUser(user);
        
        Map<String, Transform.User> relations=new HashMap<>();
        relations.put("r0", Transform.User.newBuilder().setUid("u0").build());
        relations.put("r1", Transform.User.newBuilder().setUid("u1").build());
        
        session.addFriends(Transform.User.newBuilder().setUid("f1").addTags("f1").addTags("f2"));
        session.addFriends(Transform.User.newBuilder().setUid("f2").addTags("f1").addTags("f2"));
        session.putAllRelations(relations);
        Session session1=trans.messageToJava(session.build(), Session.class);
        
        assertEquals(new String(session1.user.avatar),"abc");
        assert session1.user.uid.equals("uu");
        assert session1.user.enabled1;
        assertArrayEquals(session1.user.tags.toArray(), new String[]{"aa", "bb"});
        session=Transform.SessionResponse.newBuilder();
        Transform.SessionResponse response=trans.javaToMessage(session1, session).build();
        assert response.getUser().getUid().equals("uu");

    }
    @Test
    public void testMessageToBean()  {
        Transformer trans=new Transformer();
        Transform.User.Builder user= Transform.User.newBuilder().setUid("uu").addTags("aa").addTags("bb")
                .setLoginTime(LocalDateTime.now().toString())
                .setLevelType(Transform.UserLevelType.LV0)
                .setAvatar(ByteString.copyFrom("abc".getBytes()))
                .setEnabled(true);
        Transform.SessionResponse.Builder session= Transform.SessionResponse.newBuilder();
        session.setUser(user);
        Map<String, Transform.User> relations=new HashMap<>();
        relations.put("r0", Transform.User.newBuilder().setUid("u0").build());
        relations.put("r1", Transform.User.newBuilder().setUid("u1").build());
        session.addFriends(Transform.User.newBuilder().setUid("f1").addTags("f1").addTags("f2"));
        session.addFriends(Transform.User.newBuilder().setUid("f2").addTags("f1").addTags("f2"));
        session.putAllRelations(relations);
        Session session1=trans.messageToBean(session.build(), Session.class);
        assertEquals(new String(session1.user.avatar),"abc");
        assert session1.user.uid.equals("uu");
        assert session1.user.enabled1;
        assertArrayEquals(session1.user.tags.toArray(), new String[]{"aa", "bb"});
        session=Transform.SessionResponse.newBuilder();
        Transform.SessionResponse response=trans.beanToMessage(session1, session).build();
        assert response.getUser().getUid().equals("uu");

    }
    @Test
    public void testMessageToMap(){
        Transformer trans=new Transformer();
        Transform.User.Builder user= Transform.User.newBuilder().setUid("uu").addTags("aa").addTags("bb")
                .setLoginTime(LocalDateTime.now().toString())
                .setLevelType(Transform.UserLevelType.LV0)
                .setAvatar(ByteString.copyFrom("abc".getBytes()))
                .setEnabled(true);
        Transform.SessionResponse.Builder session= Transform.SessionResponse.newBuilder();
        session.setUser(user);
        Map<String, Transform.User> relations=new HashMap<>();
        relations.put("r0", Transform.User.newBuilder().setUid("u0").build());
        relations.put("r1", Transform.User.newBuilder().setUid("u1").build());
        session.addFriends(Transform.User.newBuilder().setUid("f1").addTags("f1").addTags("f2"));
        session.addFriends(Transform.User.newBuilder().setUid("f2").addTags("f1").addTags("f2"));
        session.putAllRelations(relations);
        Map map=trans.messageToMap(session.build());
        Transform.SessionResponse.Builder session2=trans.mapToMessage(map, Transform.SessionResponse.newBuilder());
        Session session1=trans.messageToBean(session2.build(), Session.class);
        assertEquals(new String(session1.user.avatar),"abc");
        assert session1.user.uid.equals("uu");
        assert session1.user.enabled1;
        assertArrayEquals(session1.user.tags.toArray(), new String[]{"aa", "bb"});
        session=Transform.SessionResponse.newBuilder();
        Transform.SessionResponse response=trans.beanToMessage(session1, session).build();
        assert response.getUser().getUid().equals("uu");
    }

    //    @Test
    public void testSimple(){
        Transformer trans=new Transformer();
        Transform.User.Builder user= Transform.User.newBuilder();
        Object data=new Object(){
            public String uid="aa";
            public LeveType level_type=LeveType.LV0;
        };
        Class clz=data.getClass();
        Field[] fields=clz.getDeclaredFields();
        trans.javaToMessage(data, user);
        assert user.build().getUid()=="aa";
    }
}
