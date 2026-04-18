package com.fxs.aiagent.chatMemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileBasedChatMemory implements ChatMemory {
    private final String BASE_DIR;
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        return kryo;
    });

    public FileBasedChatMemory(String dir) {
        this.BASE_DIR = dir;
        File baseDir = new File(dir);
        if (!baseDir.exists())
            baseDir.mkdirs();
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        List<Message> messages1 = getConversation(conversationId);
        messages1.addAll(messages);
        saveConversation(conversationId, messages1);
    }

    @Override
    public List<Message> get(String conversationId) {
        return getConversation(conversationId);
    }

    /*public List<Message> get(String conversationId, int lastN) {
        List<Message> messages = getConversation(conversationId);
        return messages.stream()
                .skip(messages.size() - lastN)
                .toList();
    }*/

    @Override
    public void clear(String conversationId) {
        File file = getConvertFile(conversationId);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 根据会话Id获取消息集合
     * 
     * @param conversationId
     * @return
     */
    private List<Message> getConversation(String conversationId) {
        File file = getConvertFile(conversationId);
        List<Message> messages = new ArrayList<>();
        if (file.exists() && file.length() > 0) {
            try (Input input = new Input(new FileInputStream(file))) {
                messages = kryoThreadLocal.get().readObject(input, ArrayList.class);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Failed to read conversation: " + conversationId, e);
            }
        }
        return messages;
    }

    /**
     * 将消息集合保存到会话中
     * 
     * @param conversationId
     * @param messages
     */
    private void saveConversation(String conversationId, List<Message> messages) {
        File file = getConvertFile(conversationId);
        try (Output output = new Output(new FileOutputStream(file))) {
            kryoThreadLocal.get().writeObject(output, messages);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to save conversation: " + conversationId, e);
        }
    }

    /**
     * 通过会话id拿到这个会话存储的文件
     * 
     * @param conversationId
     * @return
     */
    private File getConvertFile(String conversationId) {
        return new File(BASE_DIR, conversationId + ".kryo");
    }
}
