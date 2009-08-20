/*
 * Created on 01.03.2007
 *
 */
package de.d3web.persistence.xml.loader.rules;

import java.util.List;

import org.w3c.dom.Node;

import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.RuleFactory;
import de.d3web.kernel.psMethods.dialogControlling.PSMethodDialogControlling;
import de.d3web.persistence.xml.loader.ActionContentFactory;
import de.d3web.persistence.xml.loader.KBLoader;

/**
 * @author atzmueller
 */
public class ContraIndicationActionPersistenceHandler implements
        RuleActionPersistenceHandler {
    private static ContraIndicationActionPersistenceHandler instance = new ContraIndicationActionPersistenceHandler();

    private ContraIndicationActionPersistenceHandler() {
        super();
    }

    public static ContraIndicationActionPersistenceHandler getInstance() {
        return instance;
    }

    public Class getContext() {
        return PSMethodDialogControlling.class;
    }

    public String getName() {
        return "ActionContraIndication";
    }

    public RuleComplex getRuleWithAction(Node node, String id,
            KBLoader kbLoader, Class context) {
        List ac = ActionContentFactory.createActionContraIndicationContent(
                node, kbLoader);
        return RuleFactory.createContraIndicationRule(id, ac, null, null);
    }
}