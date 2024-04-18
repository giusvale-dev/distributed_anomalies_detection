/**
 * MIT No Attribution
 *
 *Copyright 2024 Giuseppe Valente <valentepeppe@gmail.com>
 *
 *Permission is hereby granted, free of charge, to any person obtaining a copy of this
 *software and associated documentation files (the "Software"), to deal in the Software
 *without restriction, including without limitation the rights to use, copy, modify,
 *merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 *permit persons to whom the Software is furnished to do so.
 *
 *THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 *PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package it.uniroma1;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import it.uniroma1.message.MessageProducer;

public class MessageProducerTest {
    
    @Test
    public void testSendMessage() {
        
        MessageProducer producer = null;
        try {
             producer = new MessageProducer();
            
        } catch(Exception e) {
            assertTrue(false);
        }
        
        assertNotNull(producer);
        try {
            producer.sendMessage("HelloWorld!");
        } catch(Exception e) {
            assertTrue(false);
        }
    }

}
