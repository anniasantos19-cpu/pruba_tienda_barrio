document.addEventListener("DOMContentLoaded", () => {
    const menuToggle = document.querySelector("[data-menu-toggle]");
    const sidebar = document.querySelector(".sidebar");

    if (menuToggle && sidebar) {
        menuToggle.addEventListener("click", () => {
            const isOpen = sidebar.classList.toggle("is-open");
            menuToggle.setAttribute("aria-expanded", String(isOpen));
        });

        document.addEventListener("click", (event) => {
            if (!sidebar.contains(event.target) && !menuToggle.contains(event.target)) {
                sidebar.classList.remove("is-open");
                menuToggle.setAttribute("aria-expanded", "false");
            }
        });
    }

    document.querySelectorAll("[data-toggle-form]").forEach((button) => {
        button.addEventListener("click", () => {
            const panel = document.querySelector(button.dataset.toggleForm);
            if (!panel) {
                return;
            }
            panel.hidden = !panel.hidden;
            button.setAttribute("aria-expanded", String(!panel.hidden));
            if (!panel.hidden) {
                panel.querySelector("input:not([type=hidden]), select")?.focus();
            }
        });
    });

    document.querySelectorAll("[data-add-line]").forEach((button) => {
        button.addEventListener("click", () => {
            const container = document.querySelector(button.dataset.addLine);
            const firstLine = container?.querySelector("[data-operation-line]");
            if (!container || !firstLine) {
                return;
            }
            const newLine = firstLine.cloneNode(true);
            newLine.querySelectorAll("select").forEach((select) => {
                select.selectedIndex = 0;
            });
            newLine.querySelectorAll("input").forEach((input) => {
                input.value = input.dataset.defaultValue || "";
            });
            container.append(newLine);
        });
    });

    document.addEventListener("click", (event) => {
        const button = event.target.closest("[data-remove-line]");
        if (!button) {
            return;
        }
        const container = button.closest("[data-operation-lines]");
        const line = button.closest("[data-operation-line]");
        if (container.querySelectorAll("[data-operation-line]").length > 1) {
            line.remove();
            return;
        }
        line.querySelectorAll("select").forEach((select) => {
            select.selectedIndex = 0;
        });
        line.querySelectorAll("input").forEach((input) => {
            input.value = input.dataset.defaultValue || "";
        });
    });

    document.querySelectorAll("[data-table-filter]").forEach((input) => {
        const table = document.querySelector(input.dataset.tableFilter);
        if (!table) {
            return;
        }

        input.addEventListener("input", () => {
            const query = input.value.trim().toLocaleLowerCase();
            table.querySelectorAll("tbody tr[data-search-row]").forEach((row) => {
                row.hidden = !row.textContent.toLocaleLowerCase().includes(query);
            });
        });
    });

    document.querySelectorAll("[data-confirm-delete]").forEach((form) => {
        form.addEventListener("submit", (event) => {
            const message = form.dataset.confirmMessage || "¿Deseas eliminar esta cuenta? Esta acción no se puede deshacer.";
            if (!window.confirm(message)) {
                event.preventDefault();
            }
        });
    });
});
