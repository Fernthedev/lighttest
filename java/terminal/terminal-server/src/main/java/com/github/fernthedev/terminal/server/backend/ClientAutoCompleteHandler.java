package com.github.fernthedev.terminal.server.backend;

import com.github.fernthedev.lightchat.core.data.LightCandidate;
import com.github.fernthedev.lightchat.server.Server;
import com.github.fernthedev.terminal.server.ServerTerminal;
import com.github.fernthedev.terminal.server.command.Command;
import com.github.fernthedev.terminal.server.command.TabExecutor;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ClientAutoCompleteHandler {

    private Server server;

    public List<LightCandidate> handleLine(List<String> words) {
        List<LightCandidate> candidates = new ArrayList<>();



        if(words.size() == 1) {
            for (Command command : ServerTerminal.getCommands()) {
                String string = command.getName();
                candidates.add(new LightCandidate(string, string, null, null, null, null, true));
            }
        }else{
            String c = words.get(0);
            Command curCommand = null;

            for(Command command : ServerTerminal.getCommands()) {
                if(command.getName().equalsIgnoreCase(c)) {
                    curCommand = command;
                    break;
                }
            }

            if(curCommand instanceof TabExecutor) {
                TabExecutor tabExecutor = (TabExecutor) curCommand;

                List<String> args = new ArrayList<>(words);

                if(args.isEmpty()) return candidates;

                args.remove(0); // Remove the command as first argument

                @NonNull List<String> completions = tabExecutor.getCompletions(args.toArray(new String[0]));

                for (String string : completions) {
                    candidates.add(new LightCandidate(string, string, null, null, null, null, true));
                }
            }
        }

        return candidates;
    }

}
