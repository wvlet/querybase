/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { spawnSync } from "child_process";
import { defineConfig } from "vite";

function isDev() {
    return process.env.NODE_ENV !== "production";
}

function printSbtTask(task) {
    const args = ["--error", "--batch", "-Dsbt.supershell=false", `print ${task}`];
    const options = {
        cwd: "..",
        stdio: [
            "ignore", // StdIn.
            "pipe", // StdOut.
            "inherit", // StdErr.
        ],
    };
    const result = process.platform === 'win32'
        ? spawnSync("sbt.bat", args.map(x => `"${x}"`), {shell: true, ...options})
        : spawnSync("sbt", args, options);

    if (result.error)
        throw result.error;
    if (result.status !== 0)

        throw new Error(`sbt process failed with exit code ${result.status}`);
    return result.stdout.toString('utf8').trim();
}

const replacementForPublic = isDev()
    ? printSbtTask("ui/publicDev")
    : printSbtTask("ui/publicProd");

export default defineConfig({
    build: {
      watch: {
          include: '**',
      }
    },
    server: {
        open: true,
        proxy: {
            '^/wvlet.querybase.api.v1.*': 'http://127.0.0.1:8080'
        }
    },
    resolve: {
        alias: [
            {
                find: "@public",
                replacement: replacementForPublic
            },
        ],
    },
});
