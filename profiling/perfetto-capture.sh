adb shell perfetto --txt -c - \
  -o /data/misc/perfetto-traces/${1}.perfetto-trace <<'EOF'
buffers { size_kb: 32768 fill_policy: RING_BUFFER }
duration_ms: 15000
data_sources {
  config {
    name: "linux.ftrace"
    ftrace_config {
      atrace_apps: "org.jraf.linez0rz9000.profiling"
      atrace_categories: "view"
      atrace_categories: "gfx"
      atrace_categories: "input"
      ftrace_events: "sched/sched_switch"
      ftrace_events: "sched/sched_wakeup"
    }
  }
}
data_sources { config { name: "android.surfaceflinger.frametimeline" } }
EOF

adb pull /data/misc/perfetto-traces/${1}.perfetto-trace profiling
