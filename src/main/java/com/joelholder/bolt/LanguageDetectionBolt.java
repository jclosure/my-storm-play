package com.joelholder.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import org.apache.commons.lang.StringUtils;

import org.apache.tika.language.*;
import java.util.Map;
import java.util.StringTokenizer;

public class LanguageDetectionBolt extends BaseRichBolt {
    private OutputCollector _collector;


    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        _collector = outputCollector;

    }

    @Override
    public void execute(Tuple tuple) {
    	
    	// declared fields can be picked off like this
    	String text = tuple.getStringByField("message");
    	/*String text = tuple.getStringByField("tweet");*/
        
        // opting to grab it from the index here...
    	// String text = tuple.getValue(0).toString();
    	
        String language = "UNKNOWN";
        LanguageIdentifier li = new LanguageIdentifier(text);
        //if (li.isReasonablyCertain())
            language = li.getLanguage();
        _collector.emit(new Values(text, language));


        // Confirm that this tuple has been treated.
        _collector.ack(tuple);

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("message","language"));
    }

    @Override
    public void cleanup() {
        super.cleanup();

    }
}
